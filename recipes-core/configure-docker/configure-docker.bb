FILESEXTRAPATHS:prepend := "${FILE_DIRNAME}/files:"

DESCRIPTION = "Setup script for Docker"

DEBIAN_DEPENDS = "docker.io"

inherit dpkg-raw

SRC_URI = " \
    file://postinst \
    file://http-proxy.conf \
"

do_install() {
    install -v -d ${D}/etc/systemd/system/docker.service.d
    install -v -m 644 ${WORKDIR}/http-proxy.conf ${D}/etc/systemd/system/docker.service.d/
}
