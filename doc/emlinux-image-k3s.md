# How to use meta-emlinux-k3s

## Index
- [Prerequisite](#prerequisite)
- [Clone repositories](#clone-repositories)
- [Build EMLinux](#build-emlinux)
- [Run EMLinux](#run-emlinux)
- [Confirm operation](#confirm-operation)

## Prerequisite
- Install the packeges that are needed to build EMLinux.
  - https://github.com/miraclelinux/meta-emlinux/tree/bookworm
- In this article, created the following user and directory.
  - User: emlinux
  - Home directory: /home/emlinux

## Clone repositories
1. Create a directory.
   ```sh
   mkdir -p /home/emlinux/github/emlinux/bookworm/test/repos
   ```
1. Move to the following directory.
   ```
   cd /home/emlinux/github/emlinux/bookworm/test/repos
   ```
1. Clone the following repositories.
   ```sh
   git clone -b bookworm https://github.com/miraclelinux/meta-emlinux.git meta-emlinux
   ```
   ```sh
   git clone https://github.com/hiromotai7/meta-emlinux-flavors.git
   ```
1. Edit the following files.
   - If you have a proxy server, modify http-proxy.conf.
     ```sh
     vim /home/emlinux/github/emlinux/bookworm/test/repos/meta-emlinux-flavors/recipes-core/configure-k3s/files/k3s.service.env
     ```
     ```
     HTTP_PROXY=http://your-proxy-server.com:80
     HTTPS_PROXY=http://your-proxy-server.com:80
     NO_PROXY=localhost,127.0.0.1
     ```
   - If you don't have a proxy server, remove the following line.
     ```sh
     vim /home/emlinux/github/emlinux/bookworm/test/repos/meta-emlinux-flavors/recipes-core/configure-k3s/configure-k3s.bb
     ```
     - Before
       ```
       SRC_URI = " \
           ${PKG_URL}/${PKG_NAME};name=k3s-arm64 \
           file://k3s.service \
           file://k3s.service.env \
           file://postinst \
           file://rules \
       "
       ```
     - After
       ```
       SRC_URI = " \
           ${PKG_URL}/${PKG_NAME};name=k3s-arm64 \
           file://k3s.service \
           file://postinst \
           file://rules \
       "
       ```

## Build EMLinux
1. Move to the following directory.
   ```sh
   cd /home/emlinux/github/emlinux/bookworm/test/repos/meta-emlinux/docker
   ```
1. Start a container to build EMLinux.
   ```sh
   ./run.sh
   ```
1. Run `source` command to prepare build environment.
   ```sh
   source repos/meta-emlinux/scripts/setup-emlinux build-k3s
   ```
1. Check if meta-emlinux-flavors is added as a layer. 
   ```
   $ bitbake-layers show-layers 
   NOTE: Starting bitbake server...
   layer                 path                                                                    priority
   ========================================================================================================
   core                  /home/build/work/build/../repos/isar/meta                               5
   emlinux               /home/build/work/build/../repos/meta-emlinux                            12
   cip-core              /home/build/work/build/../repos/isar-cip-core                           6
   meta-emlinux-flavors  /home/build/work/build/../repos/meta-emlinux-flavors                    50
   ```
   - If meta-emlinux-flavors is not appeared, please run the following command.
     ```sh
     echo 'BBLAYERS += "${TOPDIR}/../repos/meta-emlinux-flavors"' >> conf/bblayers.conf
     ```
1. Add MACHINE parameter as below.
   ```sh
   echo 'MACHINE = "qemu-arm64-k3s"' >> conf/local.conf
   ```
   - FIXME
1. Build EMLinux.
   ```sh
   bitbake emlinux-image-k3s
   ```
1. Exit from the container.
   ```
   exit
   ```

## Run EMLinux
### QEMU
<!-- It's annoying to modify file name again and again...
1. EMLinux files have been created on the following directory.
   ```sh
   cd /home/emlinux/github/emlinux/bookworm/test/build/tmp/deploy/images/qemu-arm64-k3s
   ```
   ```
   $ ls -lh
   total 735M
   -rw-r--r-- 1 emlinux emlinux 371K Oct  5 06:22 emlinux-image-mono-emlinux-bookworm-qemu-arm64.dpkg_status
   -rw-r--r-- 1 emlinux emlinux 774M Oct  5 06:22 emlinux-image-mono-emlinux-bookworm-qemu-arm64.ext4
   -rw-r--r-- 2 emlinux emlinux 9.2M Oct  5 06:22 emlinux-image-mono-emlinux-bookworm-qemu-arm64-initrd.img
   -rw-r--r-- 1 emlinux emlinux  21K Oct  5 06:22 emlinux-image-mono-emlinux-bookworm-qemu-arm64.manifest
   -rw-r--r-- 2 emlinux emlinux  37M Oct  5 06:22 emlinux-image-mono-emlinux-bookworm-qemu-arm64-vmlinux
   ```
-->
1. Create a TAP device using sudo user.
   ```sh
   sudo ip tuntap add tap0 mode tap
   ```
   ```sh
   sudo ip link set tap0 promisc on
   ```
   ```sh
   sudo ip link set dev tap0 master virbr0
   ```
   ```sh
   sudo ip link set dev tap0 up
   ```
1. Move to the build directory.
   ```sh
   cd /home/emlinux/github/emlinux/bookworm/test/build-k3s
   ```
1. Create run-vm.sh.
   ```sh
   touch run-vm.sh
   ```
   ```sh
   chmod +x run-vm.sh
   ```
1. Edit run-vm.sh as below.
   ```
   image=emlinux-image-k3s-emlinux-bookworm-qemu-arm64-k3s
   path=./tmp/deploy/images/qemu-arm64-k3s

   qemu-system-aarch64 \
   -net nic \
   -net tap,ifname=tap0,script=no \
   -drive id=disk0,file=$path/$image.ext4,\if=none,format=raw \
   -device virtio-blk-device,drive=disk0 -show-cursor -device VGA,edid=on \
   -device qemu-xhci \
   -device usb-tablet \
   -device usb-kbd \
   -object rng-random,filename=/dev/urandom,id=rng0 \
   -device virtio-rng-pci,rng=rng0 \
   -nographic \
   -machine virt \
   -cpu cortex-a57 \
   -m 2G \
   -serial mon:stdio \
   -serial null \
   -kernel $path/$image-vmlinux \
   -initrd $path/$image-initrd.img \
   -append 'root=/dev/vda rw highres=off console=ttyS0 mem=2G ip=dhcp console=ttyAMA0'
   ```
1. Start a virtual machine.
   ```sh
   ./run-vm.sh
   ```
   ```
   [    0.000000] Booting Linux on physical CPU 0x0000000000 [0x411fd070]
   [    0.000000] Linux version 6.1.102-cip26 (isar-users@googlegroups.com) (aarch64-linux-gnu-gcc (Debian 12.2.0-14) 12.2.0, GNU ld (GNU Binutils for Debian) 2.40) #1 SMP PREEMPT Thu, 01 Jan 1970 01:00:00 +0000
   [    0.000000] Machine model: linux,dummy-virt
   (snip)
   EMLinux3 login:
   ```
1. Login to EMLinux.
   - user: root
   - password: root

## Confirm operation
### On EMLinux
1. Check the status of K3s.
   ```
   root@EMLinux3:~# k3s-arm64 kubectl get node 
   NAME       STATUS   ROLES                  AGE   VERSION
   emlinux3   Ready    control-plane,master   19s   v1.31.1+k3s1
   ```
1. Check the status of all containers.
   ```
   root@EMLinux3:~# k3s-arm64 kubectl get pod --all-namespaces 
   NAMESPACE     NAME                                      READY   STATUS      RESTARTS   AGE
   kube-system   coredns-56f6fc8fd7-wn7gr                  1/1     Running     0          2m17s
   kube-system   helm-install-traefik-9snts                0/1     Completed   2          2m18s
   kube-system   helm-install-traefik-crd-tpd9b            0/1     Completed   0          2m18s
   kube-system   local-path-provisioner-846b9dcb6c-787kg   1/1     Running     0          2m17s
   kube-system   metrics-server-5985cbc9d7-vsdsj           1/1     Running     0          2m17s
   kube-system   svclb-traefik-ea97fb4a-mlgw6              2/2     Running     0          41s
   kube-system   traefik-8dc7cf49b-b5lgv                   1/1     Running     0          43s
   ```
1. Run the following command and copy the command result on some text file.
   ```
   root@EMLinux3:~# k3s-arm64 kubectl config view --raw
   apiVersion: v1
   clusters:
   - cluster:
   (snip)
   ```

### On the other machine
1. Create a user (e.g., `kubeuser`) to run kubectl command on the other machine.
1. Login with `kubeuser` account and create `.kube` directory on the home directory (e.g., `/home/kubeuser`).
   ```sh
   mkdir .kube
   ```
1. Create `.kube/config`, paste the result of `k3s-arm64 kubectl config view --raw` and change IP address from `127.0.0.1` to IP address of your EMLinux (in this article, IP address is `192.168.122.77`).
   ```sh
   vim .kube/config
   ```
   ```
   apiVersion: v1
   clusters:
   - cluster:
   (snip)
       server: https://192.168.122.77:6443
   (snip)   
   ```
1. Download `kubectl`.
   ```sh
   curl -LO https://dl.k8s.io/release/v1.31.1/bin/linux/amd64/kubectl
   ```
   - Reference: https://kubernetes.io/docs/tasks/tools/install-kubectl-linux/#install-kubectl-binary-with-curl-on-linux
1. Run the following command to check if you can get K3s status.
   ```
   $ ./kubectl get node
   NAME       STATUS   ROLES                  AGE   VERSION
   emlinux3   Ready    control-plane,master   27m   v1.31.1+k3s1
   ```

### Create NGINX Pod
1. Create `yaml` directory on `/home/kubeuser`.
   ```sh
   mkdir yaml
   ```
1. Create `nginx.yaml` file and edit as below.
   ```sh
   vim yaml/nginx.yaml
   ```
   ```yaml
   apiVersion: v1
   kind: Pod
   metadata:
     name: nginx
     labels:
       app: nginx
   spec:
     containers:
       - name: nginx
         image: nginx
         ports:
         - containerPort: 80
   ---
   apiVersion: v1
   kind: Service
   metadata:
     name: nginx
   spec:
     type: NodePort
     ports:
       - name: nginx
         protocol: TCP
         port: 80
         targetPort: 80
         nodePort: 30080
     selector:
       app: nginx
   ```
1. Apply the manifest file to create pod and service.
   ```sh
   $ ./kubectl apply -f yaml/nginx.yaml 
   pod/nginx created
   service/nginx created
   ```
1. Check if the Pod and Service are runnging.
   ```
   $ ./kubectl get pod,svc
   NAME        READY   STATUS    RESTARTS   AGE
   pod/nginx   1/1     Running   0          2m53s

   NAME                 TYPE        CLUSTER-IP     EXTERNAL-IP   PORT(S)        AGE
   service/kubernetes   ClusterIP   10.43.0.1      <none>        443/TCP        32m
   service/nginx        NodePort    10.43.114.87   <none>        80:30080/TCP   2m53s
   ```
1. Check if you can access to NGINX.
   ```sh
   curl 192.168.122.77:30080 --noproxy 192.168.122.77
   ```
   ```
   <!DOCTYPE html>
   <html>
   <head>
   <title>Welcome to nginx!</title>
   <style>
   (snip)   
   ```
   - If you don't have a proxy server, you don't need `--noproxy` option.
