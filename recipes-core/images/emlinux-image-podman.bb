#
# Podman Linux image
#   based on EMLinux base image
#
# SPDX-License-Identifier: MIT
#

require recipes-core/images/emlinux-image-base.bb

DESCRIPTION = "Podman target root filesystem"

LICENSE = "MIT"

#
# Install recipes or self-build packages
#
IMAGE_INSTALL:append = " \
    configure-podman \
"

#
# Install Debian packages
#
IMAGE_PREINSTALL:append = " \
    ca-certificates \
    connman \
    containernetworking-plugins \
    dbus-x11 \
    podman \
    slirp4netns \
    uidmap \
"

#
# Extra space for rootfs in MB
#
ROOTFS_EXTRA = "10240"

#
# Add user to run podman command
#
USERS += "emlinux"
USER_emlinux[password] = "emlinux"
USER_emlinux[flags] = "create-home clear-text-password"
USER_emlinux[home] = "/home/emlinux"
USER_emlinux[shell] = "/bin/bash"
