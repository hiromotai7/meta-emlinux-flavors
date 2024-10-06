FILESEXTRAPATHS:prepend := "${FILE_DIRNAME}/files:"

DESCRIPTION = "Setup script for K3s"

DEBIAN_DEPENDS = "iptables"

inherit dpkg-raw

PKG_URL = "https://github.com/k3s-io/k3s/releases/download/v1.31.1+k3s1"
PKG_NAME = "k3s-arm64"

SRC_URI = " \
    ${PKG_URL}/${PKG_NAME};name=k3s-arm64 \
    file://k3s.service \
    file://k3s.service.env \
    file://postinst \
    file://rules \
"

SRC_URI[k3s-arm64.sha256sum] = "8ddcf03f00031342e187600554b52457779c5b8feb10cdf48f87900bfdb7a702"

do_install() {
    bbnote "Save k3s-arm64 on /usr/local/bin"
    install -v -d ${D}/usr/local/bin/
    install -v -m 755 ${DL_DIR}/k3s-arm64 ${D}/usr/local/bin/
    install -v -d ${D}/etc/systemd/system
    install -v -m 755 ${WORKDIR}/k3s.service ${D}/etc/systemd/system/
    install -v -m 755 ${WORKDIR}/k3s.service.env ${D}/etc/systemd/system/
}
