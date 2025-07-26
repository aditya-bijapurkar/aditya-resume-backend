#!/bin/bash

echo "Encoding StoredCredential file to base64..."

if [ ! -f "tokens/StoredCredential" ]; then
    echo "Error: tokens/StoredCredential file not found!"
    echo "Please ensure the file exists before running this script."
    exit 1
fi

ENCODED_CONTENT=$(base64 -i tokens/StoredCredential)

echo "Base64 encoded content:"
echo ""
echo "$ENCODED_CONTENT"
echo ""
echo "Copy this content and add it to your AWS Secrets Manager as 'STORED_CREDENTIAL_BASE64'"
echo ""
echo "You can also save it to a file:"
echo "echo '$ENCODED_CONTENT' > stored_credential_base64.txt" 