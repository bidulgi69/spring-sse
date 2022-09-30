import React from 'react'
import { NextRouter, useRouter } from 'next/router';
import { ResponsiveLine } from '@nivo/line'
import NavigateBeforeIcon from '@mui/icons-material/NavigateBefore';

interface NasdaqProps {
  ticker: string,
}

type Point = {
  x: number | string | Date
  y: number | string | Date
}

type LineChartProps = {
  id: string | number,
  data: Point[]
}

const Nasdaq = ({ ticker }: NasdaqProps) => {
  const router: NextRouter = useRouter()
  const [ state, setState ] = React.useState<LineChartProps>({
    id: ticker,
    data: [],
  })
  let { data } = state
  const ref = React.useRef<Point[]>(data)

  React.useEffect(() => {
    const source = new EventSource(`${process.env.SERVER_ADDRESS}/connect/${ticker}`, {
      withCredentials: true
    })
    source.addEventListener("tick", (evt) => {
      handleTick(evt.data)
    })

    //  cleanup
    return () => {
      source.close()
    }
  }, [])

  const handleTick = (tick: number) => {
    ref.current = [...ref.current, { x: tick, y: tick }]
    setState({ ...state, data: [...ref.current] })
  }

  return (
    <div style={{ display: 'flex', flexDirection: 'column', minWidth: '100%', marginTop: '8pt', marginLeft: '4pt' }}>
      <div style={{ width: '10rem', height: '10vh', marginLeft: '8pt', display: 'flex', flexDirection: 'row', justifyContent: 'space-between', alignItems: 'center' }}>
        <NavigateBeforeIcon style={{ cursor: 'pointer' }} onClick={() => router.back()} />
        <h1>{ticker}</h1>
      </div>
      <div style={{ height: '60vh' }}>
        {
          state.data.length == 0 ?
            <h2 style={{ width: '100%', textAlign: 'center' }}>Loading ticks...</h2>
            : <ResponsiveLine data={[state]} />
        }
      </div>
    </div>
  )
}

//  allowed only in page components
Nasdaq.getInitialProps = ({ query }) => {
  const { ticker } = query
  return { ticker }
}

export default Nasdaq