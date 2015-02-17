#! /usr/bin/env bash


base_dir="iris-spl"
products_dir="iris_products"
current_dir=`pwd | xargs basename`
count=0
path=`pwd | xargs dirname`

# Ensure we are in the correct directory.
if [ "$current_dir" != "$base_dir" ];
then
    echo "You must run this script fron dir \"$base_dir\"."
    exit 1
fi

# Ensure we have enough him files to proceed.
him_count=`ls *.him 2> /dev/null  | wc -l`
if [ $him_count -eq 0 ];
then
    echo "No him files in dir, generate him files first."
    exit 1
fi

for input in `ls *.him`
do
    echo 
    count_formated=`printf "%02d" $count`
    printf "Generating project.properties.%02d..." $count
    file=`printf "project.properties.%02d" $count`
    touch $file
    # Do create the file for each spl product.
    echo "name=irisProductLine$count_formated" > $file
    echo "feature-model=$path/$base_dir/featureModel.fide" >> $file
    echo "component-model=$path/$base_dir/componentModel.txt" >> $file
    echo "configuration-model=$path/$base_dir/configuration-model.xml" >> $file
    echo "instance-model=$path/$base_dir/$input" >> $file
    echo "source-dir=$path" >> $file
    # The target-dir must not be inside your source dir, otherwise we go in an infinite loop. We choose your $HOME dir.
    if [ ! -d "$HOME/$products_dir" ]; then
	mkdir $HOME/iris_products
    fi
    echo "target-dir=$HOME/$products_dir/$count_formated" >> $file
    count=$((count+1))
done
echo
