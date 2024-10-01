#
# Gnome Linux image
#   based on EMLinux base image
#
# SPDX-License-Identifier: MIT
#

require recipes-core/images/emlinux-image-base.bb

DESCRIPTION = "Gnome target root filesystem"

LICENSE = "MIT"

#
# Install recipes or self-build packages
#
IMAGE_INSTALL:append = " \
"

#
# Install Debian packages
#
IMAGE_PREINSTALL:append = " \
    gnome \
"
