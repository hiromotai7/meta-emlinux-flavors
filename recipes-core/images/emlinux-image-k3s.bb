#
# K3s Linux image
#   based on EMLinux base image
#
# SPDX-License-Identifier: MIT
#

require recipes-core/images/emlinux-image-base.bb

DESCRIPTION = "Install K3s"

LICENSE = "MIT"

#
# Install recipes or self-build packages
#
IMAGE_INSTALL:append = " \
    configure-k3s \
"

#
# Install Debian packages
#
IMAGE_PREINSTALL:append = " \
    ca-certificates \
    connman \
"

#
# Extra space for rootfs in MB 
#
ROOTFS_EXTRA = "10240"
