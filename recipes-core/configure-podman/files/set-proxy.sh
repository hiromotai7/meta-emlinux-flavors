#!/bin/sh
PROXY=http://your-proxy-server.com:80
export http_proxy=${PROXY}
export HTTP_PROXY=${PROXY}
export https_proxy=${PROXY}
export HTTPS_PROXY=${PROXY}
export ftp_proxy=${PROXY}
export FTP_PROXY=${PROXY}
export ALL_PROXY=${PROXY}
export all_proxy=${PROXY}
export no_proxy='127.0.0.1'
