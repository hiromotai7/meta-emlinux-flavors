FILESEXTRAPATHS:prepend := "${FILE_DIRNAME}/files:"

DESCRIPTION = "Setup script for Podman"

DEBIAN_DEPENDS = "podman"

inherit dpkg-raw

SRC_URI = " \
    file://containers.conf \
    file://set-proxy.sh \
"

do_install() {
    install -v -d ${D}/etc/profile.d
    install -v -m 755 ${WORKDIR}/set-proxy.sh ${D}/etc/profile.d/
    install -v -d ${D}/etc/containers
    install -v -m 644 ${WORKDIR}/containers.conf ${D}/etc/containers/
}
