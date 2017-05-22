import { gql } from 'react-apollo'

const saveSourceMutations = {
  csw: gql`
    mutation SaveCswSource($config: CswSourceConfiguration!){
      saveCswSource(sourceConfig : $config)
    }
  `,
  openSearch: gql`
    mutation SaveWfsSource($config: WfsSourceConfiguration!){
      saveWfsSource(sourceConfig : $config)
    }
  `,
  wfs: gql`
    mutation SaveOpenSearchSource($config: OpenSearchConfiguration!){
      saveOpenSearchSource(sourceConfig : $config)
    }
  `
}

const saveSource = ({ type, sourceConfig, sourceName, creds }) => {
  let finalConfig = {
    ...sourceConfig,
    sourceName: sourceName
  }

  if (creds) {
    finalConfig.creds = creds
  }

  return ({
    mutation: saveSourceMutations[type],
    variables: {
      config: finalConfig
    }
  })
}

export { saveSourceMutations, saveSource }
