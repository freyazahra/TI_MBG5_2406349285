#!/bin/sh
set -eu
set +x

: "${DEPLOY_USER:?}" "${DEPLOY_HOST:?}" "${DOCKERHUB_USERNAME:?}" "${DOCKERHUB_TOKEN:?}"
: "${CI_COMMIT_SHA:?}" "${DB_URL:?}" "${DB_USERNAME:?}" "${DB_PASSWORD:?}" "${ALLOWED_ORIGINS:?}"

emit_value() {
    encoded=$(printf '%s' "$2" | base64 | tr -d '\n')
    printf "%s='%s'\n" "$1" "$encoded"
}

{
    emit_value HUB_USER_B64 "$DOCKERHUB_USERNAME"
    emit_value HUB_TOKEN_B64 "$DOCKERHUB_TOKEN"
    emit_value IMAGE_TAG_B64 "$CI_COMMIT_SHA"
    emit_value DB_URL_B64 "$DB_URL"
    emit_value DB_USER_B64 "$DB_USERNAME"
    emit_value DB_PASSWORD_B64 "$DB_PASSWORD"
    emit_value ORIGINS_B64 "$ALLOWED_ORIGINS"
    cat ci/deploy-remote.sh
} | ssh -i ~/.ssh/id_deploy -o IdentitiesOnly=yes -o StrictHostKeyChecking=yes \
    "$DEPLOY_USER@$DEPLOY_HOST" 'sh -s'
