import React from 'react'
import { connect } from 'react-redux'
import { withApollo } from 'react-apollo'

import { getDiscoveryType, getDiscoveredEndpoints, getChosenEndpoint } from '../reducer'
import { getAllConfig, getMessages } from 'admin-wizard/reducer'
import { changeStage, setDiscoveredEndpoints, setChosenEndpoint, startSubmitting, endSubmitting } from '../actions'
import { discoverSources, filterAvailableEndpoints } from '../graphql-queries/source-discovery'

import Title from 'components/Title'
import Description from 'components/Description'
import ActionGroup from 'components/ActionGroup'
import Action from 'components/Action'
import Message from 'components/Message'

import { NavPanes, SourceRadioButtons } from '../components'

const SourceSelectionStageView = ({ messages, changeStage, discoveryType, discoveredEndpoints = {}, configs, client, setDiscoveredEndpoints, chosenEndpoint, setChosenEndpoint, startSubmitting, endSubmitting }) => {
  if (Object.keys(discoveredEndpoints).length !== 0) {
    return (
      <NavPanes backClickTarget='discoveryStage' forwardClickTarget='confirmationStage'>
        <Title>
          Sources Found!
        </Title>
        <Description>
          Choose which sources to add.
        </Description>
        <SourceRadioButtons options={discoveredEndpoints} valueSelected={chosenEndpoint} onChange={setChosenEndpoint} />
        {messages.map((msg, i) => <Message key={i} {...msg} />)}
        <ActionGroup>
          <Action primary label='Next' disabled={chosenEndpoint === ''} onClick={() => changeStage('confirmationStage')} />
        </ActionGroup>
      </NavPanes>
    )
  } else {
    return (
      <NavPanes backClickTarget='discoveryStage' forwardClickTarget='manualEntryStage'>
        <Title>
          No Sources Found
        </Title>
        <Description>
          No sources were found at the given location. Try again or go back to enter a different address.
        </Description>
        {messages.map((msg, i) => <Message key={i} {...msg} />)}
        <ActionGroup>
          <Action primary label='Try Again' onClick={() => {
            startSubmitting()
            client.query(discoverSources({ configs, discoveryType }))
              .then(({data}) => {
                endSubmitting()
                setDiscoveredEndpoints(filterAvailableEndpoints(data))
              })
              .catch((errors) => {
                endSubmitting()
                console.log(errors)
              })
          }
          } />
        </ActionGroup>
      </NavPanes>
    )
  }
}

let SourceSelectionStage = connect((state) => ({
  messages: getMessages(state, 'sourceSelectionStage'),
  discoveryType: getDiscoveryType(state),
  discoveredEndpoints: getDiscoveredEndpoints(state),
  configs: getAllConfig(state),
  chosenEndpoint: getChosenEndpoint(state)
}), {
  changeStage,
  setDiscoveredEndpoints,
  setChosenEndpoint,
  startSubmitting,
  endSubmitting
})(SourceSelectionStageView)

export default withApollo(SourceSelectionStage)
