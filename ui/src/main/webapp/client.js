import {ApolloClient, createNetworkInterface} from 'react-apollo'

const reduxRootSelector = (state) => state.get('apollo')
const networkInterface = createNetworkInterface({
  uri: '/admin/beta/graphql',
  opts: {
    credentials: 'same-origin'
  }
})

const errors = {
  kind: 'Field',
  alias: null,
  name: {kind: 'Name', value: 'errors'},
  arguments: [],
  directives: [],
  selectionSet: null
}

networkInterface.use([{
  applyMiddleware ({ request }, next) {
    setTimeout(() => {
      request.query.definitions[0].selectionSet.selections.push(errors)
    }, 0)
    next()
  }
}])

networkInterface.useAfter([{
  applyAfterware ({ response }, next) {
    response.json()
      .then((json) => {
        response.json = () => new Promise((resolve) => {
          if (json.errors !== undefined) {
            resolve({ data: { ...json.data, errors: json.errors } })
          } else {
            resolve(json)
          }
        })
        next()
      })
  }
}])

export default new ApolloClient({ networkInterface, reduxRootSelector })

