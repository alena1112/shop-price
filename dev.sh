docker container stop shop-price-container
docker rm shop-price-container
docker build -t shop-price-docker:latest .
docker run -d --name shop-price-container -p 8080:8080 shop-price-docker
docker exec -ti shop-price-container /bin/sh
#tail -f shop_price.log