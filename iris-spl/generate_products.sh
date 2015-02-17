#! /usr/bin/env bash

base_dir="iris-spl"
products_dir="iris_products"
current_dir=`pwd | xargs basename`
path=`pwd | xargs dirname`
output_log=/dev/null

function test {
    "$@"
    local status=$?
    if [ $status -ne 0 ]; then
        echo "error with $1" >&2
	exit 1
    fi
    return $status
}

usage() {
  echo
  echo "Usage: $0 [-a] [-p <list_of_products>] [-h]"
  echo
  echo "-a - Generate all products of Iris Product Line."
  echo "-p - Generate a list of products, delimited by \",\"."
  echo "-h - This help text."
  echo
}

# Ensure we are in the correct directory.
if [ "$current_dir" != "$base_dir" ];
then
    echo "You must run this script fron dir \"$base_dir\""
    exit 1
fi

# Ensure VMCode (Hephaestus) is in path
path_to_executable=$(which VMCode)
if [ ! -x "$path_to_executable" ] ; then
   echo "Hephaestus is not in PATH."
   echo "Put VMCode (Hephaestus) in PATH and then run the this script."
fi

# Ensure enough options were given.
if [ $# -lt 1 ];
then
    usage
    exit 1
fi

case $1 in
      -h) usage
          exit 0
      ;;
      -a)
	props_count=`ls project.properties.* 2> /dev/null  | wc -l`
	if [ $props_count -eq 0 ];
	then
	    echo "No project files in dir, generate project properties files first."
	    exit 1
	fi
	for input in `ls project.properties.*`
	do
	    echo "Generating product $input..."
	    echo -e "start\n$path/$current_dir/$input\ny" | VMCode &> $output_log
	    if [ $? -ne 0 ]; then
		echo "Error with VMCode, aborting!!" 
		exit 1
	    fi
	done
      ;;
      -p) 
	echo "Products"
      ;;
      ?*) 
	echo "ERROR: Unknown option."
          usage
          exit 0
      ;;
esac

