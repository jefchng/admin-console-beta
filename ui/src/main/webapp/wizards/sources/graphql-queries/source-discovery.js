import { gql } from 'react-apollo'

import { errorCode } from 'graphql-errors'

const payloadFragments = {
  cswConfigurationPayload: gql`
    fragment cswSourceConfigurationPayload on CswSourceConfigurationPayload {
      cswProfile
      sourceName
      endpointUrl
      cswOutputSchema
      cswSpatialOperator
      pid
      creds {
        username
        password
      }
    }
  `,
  openSearchConfigurationPayload: gql`
    fragment openSearchConfigurationPayload on OpenSearchConfigurationPayload {
      sourceName
      endpointUrl
      pid
      creds {
        username
        password
      }
    }
  `,
  wfsConfigurationPayload: gql`
    fragment wfsConfigurationPayload on WfsSourceConfigurationPayload {
      wfsVersion
      sourceName
      endpointUrl
      pid
      creds {
        username
        password
      }
    }
  `
}

const queryFragments = {
  discoverOpenSearch: gql`
    fragment discoverOpenSearch on Query {
      openSearch {
        discoverOpenSearch(
          address : $address,
          creds : $creds
        )
        {
          isAvailable
          sourceConfig {
            ...openSearchConfigurationPayload
          }
        }
      }
    }
    ${payloadFragments.openSearchConfigurationPayload}
  `,
  discoverWfs: gql`
    fragment discoverWfs on Query {
      wfs {
        discoverWfs(
          address : $address,
          creds : $creds
        )
        {
          isAvailable
          sourceConfig {
            ...wfsConfigurationPayload
          }
        }
      }
    }
    ${payloadFragments.wfsConfigurationPayload}
  `,
  discoverCsw: gql`
    fragment discoverCsw on Query {
      csw {
        discoverCsw(
          address : $address,
          creds : $creds
        )
        {
          isAvailable
          sourceConfig {
            ...cswSourceConfigurationPayload
          }
        }
      }
    }
    ${payloadFragments.cswConfigurationPayload}
  `
}

const queries = {
  csw: gql`
    query discovercsw ($address: Address!, $creds: Credentials) {
      ...discoverCsw
    }
    ${queryFragments.discoverCsw}
  `,
  openSearch: gql`
    query discoverOpenSearch ($address: Address!, $creds: Credentials) {
      ...discoverWfs
    }
    ${queryFragments.discoverOpenSearch}
  `,
  wfs: gql`
    query discoverWfs ($address: Address!, $creds: Credentials) {
      ...discoverWfs
    }
    ${queryFragments.discoverWfs}
  `,
  all: gql`
    query discoverAllSources ($address: Address!, $creds: Credentials) {
      ...discoverOpenSearch
      ...discoverWfs
      ...discoverCsw
    }
    ${queryFragments.discoverOpenSearch}
    ${queryFragments.discoverWfs}
    ${queryFragments.discoverCsw}
  `
}

const discoverSources = ({ configs, discoveryType }) => {
  let query = {
    query: queries.all,
    variables: {},
    fetchPolicy: 'network-only'
  }

  if (discoveryType === 'hostnamePort') {
    query.variables.address = {
      host: {
        hostname: configs.sourceHostName,
        port: configs.sourcePort
      }
    }
  } else {
    query.variables.address = {
      url: configs.endpointUrl
    }
  }

  if (configs.sourceUserName && configs.sourceUserPassword) {
    query.variables.creds = {
      username: configs.sourceUserName,
      password: configs.sourceUserPassword
    }
  }

  return query
}

// formats the graphql response in a wizard-friendly way. Update this if the graphql requests
// or api ever change
const formatResults = (response) => {
  var results = {
    configs: {},
    errors: []
  }

  // CSW flattening
  if (response.data.csw.discoverCsw) {
    results.csw = response.data.csw.discoverCsw.sourceConfig
  }

  // WFS flattening
  if (response.data.wfs.discoverWfs) {
    results.wfs = response.data.wfs.discoverWfs.sourceConfig
  }

  // OpenSearch flattening
  if (response.data.openSearch.discoverOpenSearch) {
    results.openSearch = response.data.openSearch.discoverOpenSearch.sourceConfig
  }

  // TODO: Change this once error reporting is updated. Removing these errors as they do not
  //       necessarily mean anything went wrong during hostname & port source discovery.
  //       Also deduping identical errors to make displaying errors cleaner
  response.data.errors.forEach((error) => {
    if (error.code !== errorCode.CANNOT_CONNECT && error.code !== errorCode.UNKNOWN_ENDPOINT){
      if (!results.errors.includes(error.code)) {
        results.errors.push(error.code)
      }
    }
  })

  return results
}

export { queries, queryFragments, payloadFragments, discoverSources, formatResults }
