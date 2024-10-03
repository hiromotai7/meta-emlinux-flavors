# How to use meta-emlinux-flavors

## Prerequisite
- Install the packeges that is neede to build EMLinux.
  - https://github.com/miraclelinux/meta-emlinux/tree/bookworm
- In this article, created the following user and directory.
  - User: emlinux
  - Home directory: /home/emlinux

## Clone meta-emlinux-flavors
1. Create a directory.
   ```sh
   mkdir -p /home/emlinux/github/
   ```
1. Move to the following directory.
   ```sh
   cd /home/emlinux/github/
   ```
1. Clone meta-emlinux-flavors repository.
   ```sh
   git clone https://github.com/hiromotai7/meta-emlinux-flavors.git
   ```

## Clone meta-emlinux
1. Create a directory.
   ```sh
   mkdir -p /home/emlinux/github/emlinux/bookworm
   ```
1. Move to the following directory.
   ```
   cd /home/emlinux/github/emlinux/bookworm
   ```
1. Clone meta-emlinux repository.
   ```sh
   git clone -b bookworm https://github.com/miraclelinux/meta-emlinux.git repos/meta-emlinux
   ```
1. Move to the following directory.
   ```sh
   cd repos
   ```
1. Copy meta-emlinux-flavors directory.
   ```sh
   cp -a /home/emlinux/github/meta-emlinux-flavors/ . 
   ```
1. Move to the following directory.
   ```sh
   cd /home/emlinux/github/emlinux/bookworm/repos/meta-emlinux/docker
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
1. Build EMLinux.
   ```sh
   bitbake emlinux-image-<flavor name>
   ```
   - Refer to [flavor list](../README.md#meta-emlinux-flavors).
