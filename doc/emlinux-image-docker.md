# How to use meta-emlinux-docker

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
     vim /home/emlinux/github/emlinux/bookworm/test/repos/meta-emlinux-flavors/recipes-core/configure-docker/files/http-proxy.conf
     ```
     ```
     [Service]
     Environment="HTTP_PROXY=http://your-proxy-server.com:80"
     Environment="HTTPS_PROXY=http://your-proxy-server.com:80"
     ```
   - If you don't have a proxy server, remove the following line.
     ```sh
     vim /home/emlinux/github/emlinux/bookworm/test/repos/meta-emlinux-flavors/recipes-core/images/emlinux-image-docker.bb
     ```
     - Before
       ```
       IMAGE_INSTALL:append = " \
           configure-docker \
       "
       ```
     - After
       ```
       IMAGE_INSTALL:append = " \
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
   source repos/meta-emlinux/scripts/setup-emlinux
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
   echo 'MACHINE = "qemu-arm64"' >> conf/local.conf
   ```
   - [MACHINE list](https://github.com/miraclelinux/meta-emlinux/tree/bookworm/conf/machine)
1. Build EMLinux.
   ```sh
   bitbake emlinux-image-docker
   ```
1. Exit from the container.
   ```
   exit
   ```

## Run EMLinux
### QEMU
1. EMLinux files have been created on the following directory.
   ```sh
   cd /home/emlinux/github/emlinux/bookworm/test/build/tmp/deploy/images/qemu-arm64
   ```
   ```
   $ ls -lh
   total 735M
   -rw-r--r-- 1 emlinux emlinux 371K Oct  5 06:22 emlinux-image-docker-emlinux-bookworm-qemu-arm64.dpkg_status
   -rw-r--r-- 1 emlinux emlinux 774M Oct  5 06:22 emlinux-image-docker-emlinux-bookworm-qemu-arm64.ext4
   -rw-r--r-- 2 emlinux emlinux 9.2M Oct  5 06:22 emlinux-image-docker-emlinux-bookworm-qemu-arm64-initrd.img
   -rw-r--r-- 1 emlinux emlinux  21K Oct  5 06:22 emlinux-image-docker-emlinux-bookworm-qemu-arm64.manifest
   -rw-r--r-- 2 emlinux emlinux  37M Oct  5 06:22 emlinux-image-docker-emlinux-bookworm-qemu-arm64-vmlinux
   ```
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
   cd /home/emlinux/github/emlinux/bookworm/test/build
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
   image=emlinux-image-docker-emlinux-bookworm-qemu-arm64

   qemu-system-aarch64 \
   -net nic \
   -net tap,ifname=tap0,script=no \
   -drive id=disk0,file=./tmp/deploy/images/qemu-arm64/$image.ext4,\if=none,format=raw \
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
   -kernel ./tmp/deploy/images/qemu-arm64/$image-vmlinux \
   -initrd ./tmp/deploy/images/qemu-arm64/$image-initrd.img \
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
1. Check if `docker search` can find  NGINX container image.
   ```
   root@EMLinux3:~# docker search nginx
   NAME                                     DESCRIPTION                                     STARS     OFFICIAL   AUTOMATED
   nginx                                    Official build of Nginx.                        20248     [OK]       
   (snip)
   ```
1. Run a nginx container.
   ```sh
   docker run -it -d --name nginx-test -p 80:80 nginx:latest
   ```
1. Check the IP address of the virtual machine.
   ```
   root@EMLinux3:~# ip a 
   (snip)
   2: enp0s1: <BROADCAST,MULTICAST,DYNAMIC,UP,LOWER_UP> mtu 1500 qdisc pfifo_fast state UP group default qlen 1000
       link/ether 52:54:00:12:34:56 brd ff:ff:ff:ff:ff:ff
       inet 192.168.122.77/24 metric 1024 brd 192.168.122.255 scope global dynamic enp0s1
   (snip)
   ```
1. Run curl command on the other machine.
   ```sh
   curl 192.168.122.77 --noproxy 192.168.122.77
   ```
   ```
   <!DOCTYPE html>
   <html>
   <head>
   <title>Welcome to nginx!</title>
   <style>
   (snip)
   ```
