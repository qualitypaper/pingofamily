#!/bin/bash

# =============================================================================
# Self-signed Certificate Generation Script
# -------------------------------------------------
# This script:
# 1. Uses a provided OpenSSL .cnf file to generate a self-signed root CA.
# 2. Creates a PKCS12 keystore (keystore.p12) containing the private key + cert.
# 3. Creates a PKCS12 truststore (truststore.p12) containing only the CA cert.
#
# Requirements:
#   - OpenSSL 1.1.1 or newer
#   - A valid .cnf file (e.g., openssl-root.cnf) with [req], [ca], etc.
#
# Usage:
#   ./generate-certs.sh path/to/openssl.cnf [password]
#
#   - If password is omitted, a random one is generated and printed.
# =============================================================================

set -euo pipefail

CNF_FILE="${1:-}"
PASSWORD="${2:-}"

# Helper: generate random password
generate_password() {
    openssl rand -base64 32 | tr -d /=+ | cut -c -32
}

# Validation
if [[ -z "$CNF_FILE" || ! -f "$CNF_FILE" ]]; then
    echo "Error: Please provide a valid .cnf file as the first argument."
    echo "Usage: $0 <path/to/openssl.cnf> [password]"
    exit 1
fi

if [[ -z "$PASSWORD" ]]; then
    PASSWORD=$(generate_password)
    echo "No password provided. Generated random password: $PASSWORD"
else
    echo "Using provided password for PKCS12 files."
fi

# Output files
CA_KEY="ca.key"
CA_CERT="ca.crt"
KEYSTORE="keystore.p12"
TRUSTSTORE="truststore.p12"

echo "Generating self-signed CA using $CNF_FILE..."

# Generate CA private key + self-signed certificate
openssl req -x509 \
    -config "$CNF_FILE" \
    -newkey rsa:4096 \
    -keyout "$CA_KEY" \
    -out "$CA_CERT" \
    -days 3650 \
    -nodes \
    -subj "/$(awk '
/^\[req_distinguished_name\]$/ { found=1; next }
/^\[.*\]$/ && found { found=0 }
found { print }
' "$CNF_FILE" | tr -d ' ' | tr '\n' '/' | sed 's|/$||' | sed 's:/\+$::')" \
    || { echo "Failed to generate CA"; exit 1; }

echo "CA generated: $CA_CERT (private key: $CA_KEY)"

# Create keystore.p12 (private key + cert)
openssl pkcs12 -export \
    -in "$CA_CERT" \
    -inkey "$CA_KEY" \
    -out "$KEYSTORE" \
    -name "ca" \
    -passout pass:"$PASSWORD" \
    || { echo "Failed to create keystore.p12"; exit 1; }

echo "Keystore created: $KEYSTORE (alias: ca)"

# Create truststore.p12 (CA cert only)
openssl pkcs12 -export \
    -in "$CA_CERT" \
    -nokeys \
    -out "$TRUSTSTORE" \
    -name "ca" \
    -passout pass:"$PASSWORD" \
    || { echo "Failed to create truststore.p12"; exit 1; }

echo "Truststore created: $TRUSTSTORE (contains CA cert)"

# Cleanup: remove plaintext key/cert
read -p "Remove plaintext CA key and cert? (y/N): " -n 1 -r
echo
if [[ $REPLY =~ ^[Yy]$ ]]; then
    rm -f "$CA_KEY" "$CA_CERT"
    echo "Plaintext files removed."
else
    echo "Plaintext files preserved: $CA_KEY, $CA_CERT"
fi

echo "========================================"
echo "Generation complete!"
echo "  Keystore:   $KEYSTORE   (password: $PASSWORD)"
echo "  Truststore: $TRUSTSTORE (password: $PASSWORD)"
echo "  Alias:      ca"
echo "========================================"

exit 0
