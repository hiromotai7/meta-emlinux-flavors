#
# Docker Linux image
#   based on EMLinux base image
#
# SPDX-License-Identifier: MIT
#

require recipes-core/images/emlinux-image-base.bb

DESCRIPTION = "Docker target root filesystem"

LICENSE = "MIT"

#
# Install recipes or self-build packages
#
IMAGE_INSTALL:append = " \
    configure-docker \
"

#
# Install Debian packages
#
IMAGE_PREINSTALL:append = " \
    ca-certificates \
    connman \
    docker.io \
"

#
# Extra space for rootfs in MB 
#
ROOTFS_EXTRA = "10240"
