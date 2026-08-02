import React from 'react';

export default function LeaderboardView({ leaderboard }) {
  return (
    <div className="leaderboard-container">
      <div className="leaderboard-header glass-panel">
        <div className="trophy-icon">🏆</div>
        <h1>Global Collectors Leaderboard</h1>
        <p>Ranked by verified digital vault net worth and rarest tier holdings.</p>
      </div>

      <div className="leaderboard-table-glass glass-panel">
        <div className="table-header-row">
          <span className="col-rank">Rank</span>
          <span className="col-user">Collector Profile</span>
          <span className="col-tier">Collector Tier</span>
          <span className="col-items">Items Owned</span>
          <span className="col-networth">Total Net Worth</span>
        </div>

        <div className="table-body-rows">
          {leaderboard.map((item) => {
            const isTop3 = item.rank <= 3;
            return (
              <div key={item.rank} className={`table-row-glass ${isTop3 ? 'top-rank' : ''}`}>
                <div className="col-rank">
                  <span className={`rank-badge rank-${item.rank}`}>
                    {item.rank === 1 ? '🥇 1' : item.rank === 2 ? '🥈 2' : item.rank === 3 ? '🥉 3' : `#${item.rank}`}
                  </span>
                </div>

                <div className="col-user">
                  <img src={item.avatar} alt={item.username} className="collector-avatar" />
                  <span className="collector-name">{item.username}</span>
                </div>

                <div className="col-tier">
                  <span className="collector-tier-pill">{item.tier}</span>
                </div>

                <div className="col-items">
                  <span>{item.itemsOwned} Vault Items</span>
                </div>

                <div className="col-networth">
                  <span className="networth-table-amount">₹{item.netWorth.toLocaleString('en-IN')}</span>
                </div>
              </div>
            );
          })}
        </div>
      </div>
    </div>
  );
}
