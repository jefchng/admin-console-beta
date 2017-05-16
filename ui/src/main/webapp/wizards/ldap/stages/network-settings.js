import React from 'react'

import Mount from 'react-mount'

import { graphql, gql, withApollo } from 'react-apollo'

import Stage from 'components/Stage'
import Title from 'components/Title'
import Description from 'components/Description'
import Action from 'components/Action'
import ActionGroup from 'components/ActionGroup'
import Message from 'components/Message'

import { testConnection } from '../graphql-queries'

import { setMessages } from 'admin-wizard'

import {
  Hostname,
  Port,
  Select
} from 'admin-wizard/inputs'

const NetworkSettings = (props) => {
  const {
    client,
    data,
    setDefaults,
    prev,
    next,
    submitting,
    disabled,
    messages = []
  } = props

  return (
    <Stage submitting={submitting}>
      <Mount
        on={setDefaults}
        port={636}
        encryptionMethod='LDAPS' />
      <Title>LDAP Network Settings</Title>
      <Description>
        To establish a connection to the remote LDAP store, we need the hostname of the
        LDAP machine, the port number that the LDAP service is running on, and the
        encryption method. Typically, port 636 uses LDAPS encryption and port 389 uses
        StartTLS.
      </Description>

      <Hostname id='hostName' disabled={disabled} autoFocus />
      <Port id='port' disabled={disabled} options={[389, 636]} />
      <Select id='encryptionMethod'
        label='Encryption Method'
        disabled={disabled}
        options={[ 'None', 'LDAPS', 'StartTLS' ]} />

      <ActionGroup>
        <Action
          secondary
          label='back'
          onClick={prev}
          disabled={disabled} />
        <Action
          primary
          label='next'
          onClick={ () => client.query(
            testConnection.withVariables({
              hostname: 'localhost',
              port: 8993,
              encryption: 'ldaps'
            }))
            .then(checkConnect({
              onPass: () => next,
              onFail: () => console.log('Could not connect to LDAP machine')}))
            .catch(() => console.log('Network Error'))
          }
          disabled={disabled}
          nextStageId='bind-settings'
          testId='connection' />
      </ActionGroup>

      {messages.map((msg, i) => <Message key={i} {...msg} />)}
    </Stage>
  )
}

const checkConnect = ({ onPass = () => {}, onFail = () => {} }) => ({ data }) => {
  if (data.ldap.testConnect) {
    onPass()
  } else {
    onFail()
  }
}

export default withApollo(NetworkSettings)
