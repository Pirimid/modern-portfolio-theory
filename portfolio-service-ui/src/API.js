const BASE_API = 'http://localhost:8991/';
const SEARCH_SYMBOL_API = BASE_API + 'data/search/quote';
const GET_QUOTES_API = BASE_API + 'data/quotes';
const OPTIMIZE_PORTFOLIO_API = BASE_API + 'portfolio/optimize';

export function searchQuote(symbolStr, country, searchType) {
  return fetch(`${SEARCH_SYMBOL_API}?searchTerm=${symbolStr}&country=${country}&searchType=${searchType}`);
}

export function getQuotes(symbols, searchType) {
  return fetch(GET_QUOTES_API, { 
    method: 'POST', 
    body: JSON.stringify(symbols), 
    headers: { 
        "Content-type": 'application/json; charset=UTF-8'
    }
  }); 
}


export function optimizePortfolio(searchType, country, from, to, markowitzMaxSharpeRatioAssets) {
  return fetch(OPTIMIZE_PORTFOLIO_API, { 
    method: 'POST', 
    body: JSON.stringify({
      country,
      searchType,
      from,
      to,
      markowitzMaxSharpeRatioAssets
    }), 
    headers: { 
        "Content-type": 'application/json; charset=UTF-8'
    }
  }); 
}