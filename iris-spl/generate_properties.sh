#! /usr/bin/env bash


base_dir="iris-spl"
products_dir="iris_products"
current_dir=`pwd | xargs basename`
count=0
path=`pwd | xargs dirname`

# Ensure we are in the correct directory.
if [ "$current_dir" != "$base_dir" ];
then
    echo "You must run this script fron dir \"$base_dir\""
    exit 1
fi


for input in `ls *.him`
do
    echo 
    count_formated=`printf "%02d" $count`
    printf "Generating project.properties.%02d" $count
    file=`printf "project.properties.%02d" $count`
    touch $file
    # Do create the file for each spl product.
    echo "name=irisProductLine$count_formated" > $file
    echo "feature-model=$path/$base_dir/featureModel.fide" >> $file
    echo "component-model=$path/$base_dir/componentModel.txt" >> $file
    echo "configuration-model=$path/$base_dir/configuration-model.xml" >> $file
    echo "instance-model=$path/$base_dir/$input" >> $file
    echo "source-dir=$path" >> $file
    if [ ! -d "$path/$base_dir/$products_dir" ]; then
	mkdir $path/$base_dir/iris_products
    fi
    echo "target-dir=$path/$base_dir/$products_dir/$count_formated" >> $file
    count=$((count+1))
done
echo
