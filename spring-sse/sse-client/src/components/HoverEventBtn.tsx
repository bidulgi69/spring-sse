import React from 'react'

interface HoverEventBtnProps {
  ticker: string,
  onClick: () => {}
}

const HoverEventBtn = ({ ticker, onClick }: HoverEventBtnProps) => {
  const [ isHover, setIsHover ] = React.useState<boolean>(false)
  return (
    <div style={{
      display: 'flex', justifyContent: 'center', alignItems: 'center',
      cursor: 'pointer', background: `${isHover ? 'gray' : 'transparent'} 0% 0% no-repeat padding-box`, boxShadow: '0px 0px 3px solid #000',
      width: '20rem', height: '20vh', fontSize: '21pt', fontWeight: 'bold', fontFamily: 'sans-serif' }}
         onMouseOver={() => setIsHover(true)}
         onMouseOut={() => setIsHover(false)}
         onClick={() => onClick()}
    >
      {ticker}
    </div>
  )
}

export default HoverEventBtn