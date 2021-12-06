#!/bin/bash

mkdir -p ~/.config/rclone
echo "$RF" > ~/.config/rclone/rclone.conf
cd /tmp 
time rclone copy brrbrr:/ccache/ccache.tar.gz /tmp
time tar xf ccache.tar.gz  
rm -rf ccache.tar.gz

git config --global  user.name "popoASMx"
git config --global  user.email "pratyayaborborah@gmail.com"

# Fix TimeZone
sudo ln -snf /usr/share/zoneinfo/Asia/Kolkata /etc/localtime

#tg
tg(){
	bot_api=$BT
	your_telegram_id=$1 
	msg=$2 
	curl -s "https://api.telegram.org/bot${BT}/sendmessage" --data "text=$msg&chat_id=${your_telegram_id}&parse_mode=html"
}

id=620358472 
tg $id "~10orE ROM compile status: triggered~"

# Sync
mkdir -p /tmp/rom 
cd /tmp/rom
repo init --depth=1 -u git://github.com/AospExtended/manifest.git -b 12.x
git clone https://github.com/popoASMx/local_manifest.git --depth=1 -b main .repo/local_manifests
repo sync -c --no-clone-bundle --no-tags --optimized-fetch --prune --force-sync -j 8 || repo sync -c --no-clone-bundle --no-tags --optimized-fetch --prune --force-sync -j 8

# Normal build steps
source build/envsetup.sh
lunch aosp_E-userdebug
export CCACHE_DIR=/tmp/ccache
export CCACHE_EXEC=$(which ccache)
export USE_CCACHE=1
ccache -M 20G 
ccache -o compression=true 
ccache -z
m aex -j8 &
sleep 90m
kill %1
ccache -s

id=620358472

#uploads
#up(){
#	curl --upload-file $1 https://transfer.sh/$(basename $1); echo
#	# 14 days, 10 GB limit
#}

# Rclone
time rclone copy /tmp/rom/out/target/product/E/vendo*img brrbrr:rom -P
ccache -s
cd /tmp
com () 
{ 
    tar --use-compress-program="pigz -k -$2 " -cf $1.tar.gz $1
}
time com ccache 1

# ccache upload
time rclone copy ccache.tar.gz brrbrr:ccache -P 
tg $id "~10orE ccache uploaded successfully~"
