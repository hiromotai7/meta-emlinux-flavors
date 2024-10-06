#
# Mono Linux image
#   based on EMLinux base image
#
# SPDX-License-Identifier: MIT
#

require recipes-core/images/emlinux-image-base.bb

DESCRIPTION = "Install mono-complete"

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
    mono-complete \
"

THIRD_PARTY_APT_KEYS:append = " \
    hkp://keyserver.ubuntu.com:80;rsa2048=3FA7E0328081BFF6A14DA29AA6A19B38D3D831EF \
"

DISTRO_APT_SOURCES:append = " \
    conf/distro/mono-official-stable.list \
"
