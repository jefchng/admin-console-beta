const errorCode = {
  UNKNOWN_ENDPOINT: 'UNKNOWN_ENDPOINT',
  DUPLICATE_SOURCE_NAME: 'DUPLICATE_SOURCE_NAME',
  MISSING_REQUIRED_FIELD: 'MISSING_REQUIRED_FIELD',
  EMPTY_FIELD: 'EMPTY_FIELD',
  MISSING_KEY_VALUE: 'MISSING_KEY_VALUE',
  INVALID_PORT_RANGE: 'INVALID_PORT_RANGE',
  INVALID_HOSTNAME: 'INVALID_HOSTNAME',
  INVALID_CONTEXT_PATH: 'INVALID_CONTEXT_PATH',
  FAILED_PERSIST: 'FAILED_PERSIST',
  UNSUPPORTED_ENUM: 'UNSUPPORTED_ENUM',
  CANNOT_CONNECT: 'CANNOT_CONNECT',
  FAILED_UPDATE_ERROR: 'FAILED_UPDATE',
  FAILED_DELETE_ERROR: 'FAILED_DELETE',
  INVALID_URL_ERROR: 'INVALID_URL',
  NO_EXISTING_CONFIG: 'NO_EXISTING_CONFIG',
  INVALID_URI_ERROR: 'INVALID_URI',
  UNAUTHORIZED: 'UNAUTHORIZED'
}

const friendlyMessage = {
  [errorCode.UNKNOWN_ENDPOINT]: 'No supported sources were found at this location for the specified source type.',
  [errorCode.DUPLICATE_SOURCE_NAME]: 'A source with that name already exists. Please choose a different name.',
  [errorCode.MISSING_REQUIRED_FIELD]: 'There are missing required fields.',
  [errorCode.EMPTY_FIELD]: 'There are empty required fields.',
  [errorCode.MISSING_KEY_VALUE]: `There was a problem with the request to the server: "${errorCode.MISSING_KEY_VALUE}"`,
  [errorCode.INVALID_PORT_RANGE]: 'Invalid port number.',
  [errorCode.INVALID_HOSTNAME]: 'Invalid hostname',
  [errorCode.INVALID_CONTEXT_PATH]: 'Invalid context path.',
  [errorCode.FAILED_PERSIST]: 'An error occurred while trying to save the configuration',
  [errorCode.UNSUPPORTED_ENUM]: `There was a problem with the request to the server: "${errorCode.UNSUPPORTED_ENUM}"`,
  [errorCode.CANNOT_CONNECT]: 'Could not connect to the specified server.',
  [errorCode.FAILED_UPDATE_ERROR]: 'The server failed to update the configuration',
  [errorCode.FAILED_DELETE_ERROR]: 'The server failed to delete the configuration.',
  [errorCode.INVALID_URL_ERROR]: 'Invalid URL.',
  [errorCode.NO_EXISTING_CONFIG]: 'The configuration does not exist on the server.',
  [errorCode.INVALID_URI_ERROR]: 'Invalid URI.',
  [errorCode.UNAUTHORIZED]: 'Unauthorized. A valid username & password may be required to connect.'
}

export { errorCode, friendlyMessage }