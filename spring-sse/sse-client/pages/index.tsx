import type { NextPage } from 'next'
import { NextRouter, useRouter } from 'next/router';
import React from 'react'
import axios from 'axios'
import { HoverEventBtn } from '../src/components';

const Home: NextPage = () => {
  const router: NextRouter = useRouter()
  const [ isLoading, setIsLoading ] = React.useState<boolean>(true)
  const [ tickers, setTickers ] = React.useState<string[]>([])

  React.useEffect(() => {
    getTickers()
      .then(() => setIsLoading(false))
  }, [])

  const getTickers = async () => {
    await axios.get(`${process.env.SERVER_ADDRESS}/tickers`)
      .then(res => {
        const t = res.data.toString()
        setTickers(
          t
            .split(",")
            .filter((s: string) => s.length > 0)
        )
      })
  }

  return (
    <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', justifyContent: 'center', width: '100%', height: '100vh' }}>
      <h1>Tickers</h1>
      <div style={{ marginTop: '8vh' }}>
        {
          isLoading
            ? <h2>Loading tickers...</h2>
            : tickers.map((ticker, _) => <HoverEventBtn key={ticker} ticker={ticker} onClick={() => router.push(`/nasdaq?ticker=${ticker}`)} />)
        }
      </div>
    </div>
  )
}

export default Home
