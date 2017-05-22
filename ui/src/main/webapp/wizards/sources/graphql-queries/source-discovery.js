import { gql } from 'react-apollo'

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
      url: configs.url
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

const filterAvailableEndpoints = (data) => {
  let clonedData = JSON.parse(JSON.stringify(data)) // deep clone data

  if (clonedData.errors) {
    clonedData.errors.forEach((error) => {
      if (error.code === 'UNKNOWN_ENDPOINT' || error.code === 'CANNOT_CONNECT') {
        delete clonedData[error.path[0]] // remove undiscovered endpoints
      }
    })
    delete clonedData.errors // remove errors key
  }

  delete clonedData.__typename // remove graphql helper key
  return clonedData
}

export { queries, queryFragments, payloadFragments, discoverSources, filterAvailableEndpoints }
