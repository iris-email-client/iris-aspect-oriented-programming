#! /usr/bin/env bash

base_dir="iris-spl"
products_dir="iris_products"
current_dir=`pwd | xargs basename`
path=`pwd | xargs dirname`
output_log=/dev/null

# Ensure we are in the correct directory.
if [ "$current_dir" != "$base_dir" ];
then
    echo "You must run this script fron dir \"$base_dir\""
    exit 1
fi


if [ ! -d "$HOME/$products_dir" ]; then
    echo "Directory with products does not exist!!"
    exit 1
fi

for input in $HOME/$products_dir/*/ ; do
    cd "$input"
    mvn clean package
    if [ $? -ne 0 ]; then
	echo "Error building product `basename $input`, aborting!!" 
	exit 1
    fi
done