if [ "$1" == '' ]; then
	echo "update_version x.y.z"
	exit 1;
fi

mvn -f jtransc-media-parent/pom.xml versions:set -DnewVersion=$1