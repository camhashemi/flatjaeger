REST_API_ID=cc7trhamkl

server:
	gradle run

script:
	gradle runScript \
        -DmaxPrice=1000 \
        -DminRooms=2 \
        -Ddestination="Alexanderplatz 10178, Berlin" \
        -DmaxTransitTime=60

lambda-package:
	gradle shadowJar

lambda-update-s3:
	aws s3 mv build/libs/jaeger-0.0.1-all.jar s3://flatjaeger

lambda-update-code:
	aws lambda update-function-code --function-name flatjaeger --s3-bucket flatjaeger --s3-key jaeger-0.0.1-all.jar

lambda-deploy:
	make lambda-package
	make lambda-update-s3
	make lambda-update-code

api-update:
	aws apigateway put-rest-api --rest-api-id $(REST_API_ID) --fail-on-warnings --cli-binary-format raw-in-base64-out \
		--body 'file://src/main/resources/api.yml'

api-deploy:
	aws apigateway create-deployment --rest-api-id $(REST_API_ID) --stage-name prod
