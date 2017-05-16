import { gql } from "react-apollo"

const testConnection = gql`
  query query ($hostname: Hostname!, $port: Port!, $encryption: EncryptionMethod! ) {
    ldap {
      testConnect (connection: {
        hostname: $hostname
        port: $port
        encryption: $encryption
      })
    }
  }
`

testConnection.withVariables = (variables) => ({
  query: testConnection,
  variables
})

export { testConnection }