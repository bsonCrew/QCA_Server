echo "$1"
if [ "$#" -eq 2 ]
then
	URL=$1
	URL=${URL#http*//}
	URL=${URL%/}
	echo ${URL}
	HASH=$2
	lighthouse $1 --locale ko --output json --output-path "/Users/bsu/Desktop/2022OpenSW/QCA_Server/src/output/${HASH}_output.json" --config-path "./lhConfig.js"
fi

