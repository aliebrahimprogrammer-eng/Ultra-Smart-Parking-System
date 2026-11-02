import { Accessibility, Clock, Zap } from 'lucide-react'
import { SPOT_STATUS, SPOT_TYPES, sortSpots } from '../format.js'

function Car() {
  return (
    <svg className="car" viewBox="0 0 48 88" aria-hidden="true">
      <rect x="5" y="3" width="38" height="82" rx="14" fill="#eef0f1" stroke="#aeb4b8" strokeWidth="1.5" />
      <path d="M11 29c8-5 18-5 26 0l-3 11H14z" fill="#20252b" />
      <path d="M14 44h20l3 22c-8 4-18 4-26 0z" fill="#2c3238" />
      <path d="M7 15c2-5 5-8 9-9M41 15c-2-5-5-8-9-9" stroke="#f6c945" strokeWidth="2.5" fill="none" />
      <path d="M8 76c2 3 4 5 8 6M40 76c-2 3-4 5-8 6" stroke="#d9534f" strokeWidth="2.5" fill="none" />
    </svg>
  )
}

const TYPE_ICON = { DISABLED: Accessibility, ELECTRIC: Zap }

export default function SpotGrid({ spots, selectedId, onSelect, mySpotIds }) {
  return (
    <div className="spot-grid">
      {sortSpots(spots).map((spot) => {
        const TypeIcon = TYPE_ICON[spot.type]
        const isMine = mySpotIds.has(spot.id)
        const label = `Spot ${spot.spotNumber}, ${SPOT_TYPES[spot.type]}, ${SPOT_STATUS[spot.status]}${isMine ? ', your reservation' : ''}`
        return (
          <button
            key={spot.id}
            type="button"
            className={`spot spot-${spot.status.toLowerCase()}${spot.id === selectedId ? ' spot-selected' : ''}`}
            aria-pressed={spot.id === selectedId}
            aria-label={label}
            title={label}
            onClick={() => onSelect(spot.id)}
          >
            {TypeIcon && <TypeIcon className="spot-type" size={15} aria-hidden="true" />}
            {isMine && <span className="spot-mine">Yours</span>}
            {spot.status === 'OCCUPIED' && <Car />}
            {spot.status === 'RESERVED' && <Clock size={22} aria-hidden="true" />}
            <span className="spot-number">{spot.spotNumber}</span>
          </button>
        )
      })}
    </div>
  )
}
