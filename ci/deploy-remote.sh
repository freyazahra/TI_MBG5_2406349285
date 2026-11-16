set -eu
set +x

decode() {
    printf '%s' "$1" | base64 -d
}

hub_user=$(decode "$HUB_USER_B64")
hub_token=$(decode "$HUB_TOKEN_B64")
image_tag=$(decode "$IMAGE_TAG_B64")
db_url=$(decode "$DB_URL_B64")
db_user=$(decode "$DB_USER_B64")
db_password=$(decode "$DB_PASSWORD_B64")
origins=$(decode "$ORIGINS_B64")
backend_image="docker.io/$hub_user/simbg-backend:$image_tag"
frontend_image="docker.io/$hub_user/simbg-frontend:$image_tag"

docker network inspect apap-network >/dev/null
docker inspect simbg-postgres >/dev/null

DOCKER_CONFIG=$(mktemp -d)
export DOCKER_CONFIG
cleanup() {
    rm -f "$DOCKER_CONFIG/config.json"
    rmdir "$DOCKER_CONFIG"
}
trap cleanup EXIT
printf '%s' "$hub_token" | docker login --username "$hub_user" --password-stdin docker.io
docker pull "$backend_image"
docker pull "$frontend_image"

for container in simbg-frontend simbg-backend; do
    if docker container inspect "$container" >/dev/null 2>&1; then
        docker rm -f "$container"
    fi
done

docker run -d --name simbg-backend --restart unless-stopped \
    --network apap-network \
    -e "DB_URL=$db_url" -e "DB_USERNAME=$db_user" -e "DB_PASSWORD=$db_password" \
    -e "ALLOWED_ORIGINS=$origins" "$backend_image"
docker run -d --name simbg-frontend --restart unless-stopped \
    --network apap-network -p 80:80 "$frontend_image"

attempt=0
until curl -fsS http://127.0.0.1/api/home >/dev/null; do
    attempt=$((attempt + 1))
    if [ "$attempt" -ge 60 ]; then
        docker logs --tail 50 simbg-backend
        docker logs --tail 50 simbg-frontend
        echo "Deployment failed: the API did not become ready." >&2
        exit 1
    fi
    sleep 2
done
echo "Deployment ready: frontend proxy and API are responding."
