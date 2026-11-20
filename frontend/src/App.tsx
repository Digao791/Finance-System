import { useEffect, useState } from 'react'
import type { FormEvent } from 'react'
import './App.css'
import { BarChart3, List, Settings, UserRound } from 'lucide-react'

type Page = 'transactions' | 'charts'
type TransactionType = 'INCOME' | 'EXPENSE'

type Transaction = {
  id: number
  type: TransactionType
  amount: number
  transactionDate: string
  description: string | null
}

type MonthlySummary = {
  income: number
  expense: number
  balance: number
}

type Editor = {
  id: number | null
  type: TransactionType
  amount: string
  transactionDate: string
  description: string
}

type PeriodMode = "all" | "month" | "range"
type Period = {
  from?: string
  to: string
  label: string
}

type Dashboard = {
  period: Period
  summary: MonthlySummary
  transactions: Transaction[]
}

const endpoint = '/api/transactions'

const money = new Intl.NumberFormat('pt-BR', {
  style: 'currency',
  currency: 'BRL',
})

const months = [
  'Janeiro', 'Fevereiro', 'Março', 'Abril', 'Maio', 'Junho',
  'Julho', 'Agosto', 'Setembro', 'Outubro', 'Novembro', 'Dezembro',
]

function localDate(date: Date) {
  const year = String(date.getFullYear()).padStart(4, '0')
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')

  return `${year}-${month}-${day}`
}

function monthLabel(month: string) {
  const [year, monthNumber] = month.split('-')
  return `${months[Number(monthNumber) - 1]} de ${year}`
}

function formatDate(date: string) {
  return date.split('-').reverse().join('/')
}

function monthBounds(month: string) {
  const from = `${month}-01`
  const lastDay = new Date(`${from}T00:00:00Z`)
  lastDay.setUTCMonth(lastDay.getUTCMonth() + 1, 0)

  return new URLSearchParams({
    from,
    to: `${month}-${String(lastDay.getUTCDate()).padStart(2, '0')}`,
  })
}

function monthlyPeriod(month: string): Period {
  const bounds = monthBounds(month)

  return {
    from: `${month}-01`,
    to: bounds.get('to')!,
    label: monthLabel(month),
  }
}

function periodContains(period: Period, date: string) {
  return (!period.from || date >= period.from) && date <= period.to
}

function summarize(transactions: Transaction[]): MonthlySummary {
  let income = 0
  let expense = 0

  for (const transaction of transactions) {
    const cents = Math.round(transaction.amount * 100)

    if (transaction.type === 'INCOME') {
      income += cents
    } else {
      expense += cents
    }
  }

  return {
    income: income / 100,
    expense: expense / 100,
    balance: (income - expense) / 100,
  }
}

async function apiRequest(path: string, options?: RequestInit) {
  const response = await fetch(path, options)

  if (!response.ok) {
    throw new Error(`A API retornou HTTP ${response.status}.`)
  }

  return response
}

function errorMessage(error: unknown) {
  return error instanceof Error ? error.message : 'Ocorreu um erro inesperado.'
}

function TransactionChart({ transactions }: { transactions: Transaction[] }) {
  const byDate = new Map<string, { income: number; expense: number }>()

  for (const transaction of transactions) {
    const date = transaction.transactionDate
    const totals = byDate.get(date) ?? { income: 0, expense: 0 }
    const cents = Math.round(transaction.amount * 100)

    if (transaction.type === 'INCOME') {
      totals.income += cents
    } else {
      totals.expense += cents
    }

    byDate.set(date, totals)
  }

  const days = [...byDate.entries()].sort(([first], [second]) =>
    first.localeCompare(second),
  )

  const maximum = days.reduce(
    (largest, [, totals]) => Math.max(largest, totals.income, totals.expense),
    1,
  )

  return (
    <section className="panel" aria-labelledby="chart-title">
      <div className="panel-header">
        <h2 id="chart-title">Movimentação diária</h2>
        <div className="chart-legend">
          <span className="income">● Receitas</span>
          <span className="expense">● Despesas</span>
        </div>
      </div>

      {days.length === 0 ? (
        <p className="empty-state">Nenhuma transação neste período.</p>
      ) : (
        <div className="chart-scroll">
          <ol className="daily-chart">
            {days.map(([date, { income, expense }]) => {
              const formattedDate = formatDate(date)
              const label = `${formattedDate} — receitas ${money.format(income / 100)}, despesas ${money.format(expense / 100)}`

              return (
                <li className="daily-column" key={date} title={label}>
                  <span className="sr-only">{label}</span>

                  <div className="daily-bars" aria-hidden="true">
                    <span
                      className="daily-bar daily-income"
                      style={{ height: `${(income / maximum) * 100}%` }}
                    />
                    <span
                      className="daily-bar daily-expense"
                      style={{ height: `${(expense / maximum) * 100}%` }}
                    />
                  </div>

                  <span aria-hidden="true">{formattedDate.slice(0, 5)}</span>
                </li>
              )
            })}
          </ol>
        </div>
      )}
    </section>
  )
}

export default function App() {
  const [page, setPage] = useState<Page>('transactions')
  const [draftMonth, setDraftMonth] = useState(() => localDate(new Date()).slice(0, 7))
  const [draftFrom, setDraftFrom] = useState(() => `${draftMonth}-01`)
  const [draftTo, setDraftTo] = useState(() => localDate(new Date()))
  const [filterMode, setFilterMode] = useState<PeriodMode>('month')
  const [period, setPeriod] = useState(() => monthlyPeriod(draftMonth))
  const [periodError, setPeriodError] = useState('')
  const [dashboard, setDashboard] = useState<Dashboard | null>(null)
  const [loading, setLoading] = useState(true)
  const [loadError, setLoadError] = useState('')
  const [revision, setRevision] = useState(0)

  const [editor, setEditor] = useState<Editor | null>(null)
  const [busy, setBusy] = useState(false)
  const [actionError, setActionError] = useState('')
  const [notice, setNotice] = useState('')

  useEffect(() => {
  const request = new AbortController()
  const { signal } = request

  async function loadDashboard() {
    try {
      const params = new URLSearchParams({ to: period.to })
      if (period.from) params.set('from', period.from)

      const response = await apiRequest(`${endpoint}?${params}`, { signal })
      const transactions: Transaction[] = await response.json()

      if (!signal.aborted) {
        setDashboard({
          period,
          summary: summarize(transactions),
          transactions,
        })
      }
    } catch (error) {
      if (!signal.aborted) setLoadError(errorMessage(error))
    } finally {
      if (!signal.aborted) setLoading(false)
    }
  }

  void loadDashboard()
  return () => request.abort()
}, [period, revision])

  function refresh() {
    setLoadError('')
    setLoading(true)
    if (filterMode === 'all') setPeriod(allThroughToday())
    setRevision((current) => current + 1)
  }

  function loadPeriod(next: Period) {
    setPeriodError('')
    setLoadError('')
    setNotice('')
    setLoading(true)
    setPeriod(next)
  }

  function allThroughToday(): Period {
    const today = localDate(new Date())
    return { to: today, label: `Até ${formatDate(today)}` }
  }

  function toggleAll(checked: boolean) {
    setPeriodError('')

    if (checked) {
      setFilterMode('all')
      loadPeriod(allThroughToday())
    } else {
      setFilterMode('month')
      loadPeriod(monthlyPeriod(draftMonth))
    }
  }

  function applyPeriod(event: FormEvent<HTMLFormElement>) {
  event.preventDefault()

  if (filterMode === 'month') {
    if (!/^(?!0000)\d{4}-(0[1-9]|1[0-2])$/.test(draftMonth)) {
      setPeriodError('Selecione um mês e um ano válidos.')
      return
    }

    loadPeriod(monthlyPeriod(draftMonth))
    return
  }

  if (!draftFrom || !draftTo || draftFrom > draftTo) {
    setPeriodError('A data inicial deve ser anterior ou igual à data final.')
    return
  }

  loadPeriod({
    from: draftFrom,
    to: draftTo,
    label: `${formatDate(draftFrom)} a ${formatDate(draftTo)}`,
  })
}

  function openEditor(transaction?: Transaction) {
    setActionError('')
    setNotice('')

    if (transaction) {
      setEditor({
        id: transaction.id,
        type: transaction.type,
        amount: String(transaction.amount),
        transactionDate: transaction.transactionDate,
        description: transaction.description ?? '',
      })
    } else {
      const today = localDate(new Date())

      setEditor({
        id: null,
        type: 'EXPENSE',
        amount: '',
        transactionDate: today,
        description: '',
      })
    }
  }

  async function saveTransaction(event: FormEvent<HTMLFormElement>) {
    event.preventDefault()
    if (!editor || busy) return

    const entry = editor
    setBusy(true)
    setActionError('')

    try {
      await apiRequest(entry.id === null ? endpoint : `${endpoint}/${entry.id}`, {
        method: entry.id === null ? 'POST' : 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          type: entry.type,
          amount: Number(entry.amount),
          transactionDate: entry.transactionDate,
          description: entry.description.trim() || null,
        }),
      })

      setEditor(null)
      const activePeriod = filterMode === 'all' ? allThroughToday() : period
      const isInPeriod = periodContains(activePeriod, entry.transactionDate)

      setNotice(
        isInPeriod
          ? 'Lançamento salvo.'
          : 'Lançamento salvo fora do período selecionado.',
      )

      refresh()
    } catch (error) {
      setActionError(`Não foi possível salvar. ${errorMessage(error)}`)
    } finally {
      setBusy(false)
    }
  }

  async function removeTransaction(transaction: Transaction) {
    const description = transaction.description || 'Lançamento sem descrição'
    if (busy || !window.confirm(`Remover "${description}" (${money.format(transaction.amount)})?`)) {
      return
    }

    setBusy(true)
    setActionError('')
    setNotice('')

    try {
      await apiRequest(`${endpoint}/${transaction.id}`, { method: 'DELETE' })
      setNotice('Lançamento removido.')
      refresh()
    } catch (error) {
      setActionError(`Não foi possível remover. ${errorMessage(error)}`)
    } finally {
      setBusy(false)
    }
  }

  const displayedPeriod = dashboard?.period ?? period
  const summary = dashboard?.summary
  const transactions = dashboard?.transactions ?? []

  return (
    <div className="app-layout">
      <aside className="sidebar">
        <strong className="sidebar-brand" title="Finance System">FS</strong>

        <nav className="sidebar-nav" aria-label="Menu principal">
          <button
            type="button"
            title="Transações"
            aria-label="Transações"
            aria-current={page === 'transactions' ? 'page' : undefined}
            onClick={() => setPage('transactions')}
          >
            <List size={21} aria-hidden="true" />
          </button>

          <button
            type="button"
            title="Gráficos"
            aria-label="Gráficos"
            aria-current={page === 'charts' ? 'page' : undefined}
            onClick={() => setPage('charts')}
          >
            <BarChart3 size={21} aria-hidden="true" />
          </button>

          <button type="button" title="Configurações — em breve" aria-label="Configurações" disabled>
            <Settings size={21} aria-hidden="true" />
          </button>

          <button type="button" title="Perfil — em breve" aria-label="Perfil" disabled>
            <UserRound size={21} aria-hidden="true" />
          </button>
        </nav>
      </aside>

      <main className="dashboard">
        <header className="dashboard-header">
          <div>
            <h1>{page === 'transactions' ? 'Transações' : 'Gráficos'}</h1>
            <p>{displayedPeriod.label}</p>
          </div>

          <form className="period-form" onSubmit={applyPeriod}>
  <label className="period-checkbox">
    <input
      type="checkbox"
      checked={filterMode === 'all'}
      disabled={busy || editor !== null}
      onChange={(event) => toggleAll(event.currentTarget.checked)}
    />
    Todas até hoje
  </label>

  {filterMode !== 'all' && (
    <>
      <label>
        Período
        <select
          value={filterMode}
          onChange={(event) => {
            setFilterMode(event.currentTarget.value === 'range' ? 'range' : 'month')
            setPeriodError('')
          }}
        >
          <option value="month">Mês</option>
          <option value="range">Intervalo</option>
        </select>
      </label>

      {filterMode === 'month' ? (
        <>
          <label>
            Mês
            <select
              value={draftMonth.split('-')[1]}
              onChange={(event) => {
                const selected = event.currentTarget.value
                setDraftMonth((current) => `${current.split('-')[0]}-${selected}`)
              }}
            >
              {months.map((name, index) => {
                const value = String(index + 1).padStart(2, '0')
                return <option key={value} value={value}>{name}</option>
              })}
            </select>
          </label>

          <label>
            Ano
            <input
              className="year-input"
              type="number"
              min="1"
              max="9999"
              required
              value={draftMonth.split('-')[0]}
              onChange={(event) => {
                const year = event.currentTarget.value
                setDraftMonth((current) => `${year}-${current.split('-')[1]}`)
              }}
              onBlur={() => {
                setDraftMonth((current) => {
                  const [year, selected] = current.split('-')
                  return year ? `${year.padStart(4, '0')}-${selected}` : current
                })
              }}
            />
          </label>
        </>
      ) : (
        <>
          <label>
            De
            <input
              type="date"
              value={draftFrom}
              required
              onChange={(event) => setDraftFrom(event.currentTarget.value)}
            />
          </label>

          <label>
            Até
            <input
              type="date"
              value={draftTo}
              required
              onChange={(event) => setDraftTo(event.currentTarget.value)}
            />
          </label>
        </>
      )}

      <button className="button" type="submit" disabled={busy || editor !== null}>
        Aplicar
      </button>
    </>
  )}

  {periodError && <p className="notice notice-error" role="alert">{periodError}</p>}
</form>
        </header>

        {loading && <p className="notice" role="status">Carregando {period.label}…</p>}
        {loadError && (
          <div className="notice notice-error">
            <p role="alert">Não foi possível carregar {period.label}. {loadError}</p>
            <button className="button" type="button" onClick={refresh}>Tentar novamente</button>
          </div>
        )}
        {notice && <p className="notice" role="status">{notice}</p>}
        {actionError && !editor && <p className="notice notice-error" role="alert">{actionError}</p>}

        <section className="totals" aria-label="Resumo do período exibido">
          <article className="total-card balance-card">
            <h2>Saldo</h2>
            <strong>{summary ? money.format(summary.balance) : '—'}</strong>
          </article>
          <article className="total-card">
            <h2>Receitas</h2>
            <strong className="income">{summary ? money.format(summary.income) : '—'}</strong>
          </article>
          <article className="total-card">
            <h2>Despesas</h2>
            <strong className="expense">{summary ? money.format(summary.expense) : '—'}</strong>
          </article>
        </section>

        {page === 'transactions' && (
          <section className="panel" aria-labelledby="transactions-title">
            <div className="panel-header">
              <h2 id="transactions-title">Lançamentos</h2>
              <button
                className="button button-primary"
                type="button"
                onClick={() => openEditor()}
                disabled={busy || editor !== null}
              >
                + Novo lançamento
              </button>
            </div>

            {dashboard && transactions.length === 0 && (
              <p className="empty-state">Nenhuma transação neste período.</p>
            )}

            {transactions.length > 0 && (
              <div className="table-scroll">
                <table>
                  <thead>
                    <tr>
                      <th scope="col">Data</th>
                      <th scope="col">Descrição</th>
                      <th scope="col">Tipo</th>
                      <th scope="col" className="amount">Valor</th>
                      <th scope="col">Ações</th>
                    </tr>
                  </thead>
                  <tbody>
                    {transactions.map((transaction) => {
                      const isIncome = transaction.type === 'INCOME'

                      return (
                        <tr key={transaction.id}>
                          <td>{formatDate(transaction.transactionDate)}</td>
                          <td>{transaction.description || 'Sem descrição'}</td>
                          <td>{isIncome ? 'Receita' : 'Despesa'}</td>
                          <td className={`amount ${isIncome ? 'income' : 'expense'}`}>
                            {money.format(isIncome ? transaction.amount : -transaction.amount)}
                          </td>
                          <td>
                            <div className="row-actions">
                              <button
                                className="button"
                                type="button"
                                disabled={busy || editor !== null}
                                onClick={() => openEditor(transaction)}
                              >
                                Editar
                              </button>
                              <button
                                className="button button-danger"
                                type="button"
                                disabled={busy || editor !== null}
                                onClick={() => void removeTransaction(transaction)}
                              >
                                Remover
                              </button>
                            </div>
                          </td>
                        </tr>
                      )
                    })}
                  </tbody>
                </table>
              </div>
            )}
          </section>
        )}

        {page === 'charts' && dashboard && (
          <TransactionChart transactions={dashboard.transactions} />
        )}

        {editor && (
          <section className="panel editor-panel" aria-labelledby="editor-title">
            <h2 id="editor-title">{editor.id === null ? 'Novo lançamento' : 'Editar lançamento'}</h2>

            <form className="editor-form" onSubmit={saveTransaction}>
              <label>
                Tipo
                <select
                  value={editor.type}
                  disabled={busy}
                  onChange={(event) => {
                    const type = event.currentTarget.value as TransactionType
                    setEditor({ ...editor, type })
                  }}
                >
                  <option value="EXPENSE">Despesa</option>
                  <option value="INCOME">Receita</option>
                </select>
              </label>

              <label>
                Valor (R$)
                <input
                  type="number"
                  min="0.01"
                  step="0.01"
                  required
                  disabled={busy}
                  value={editor.amount}
                  onChange={(event) => setEditor({ ...editor, amount: event.currentTarget.value })}
                />
              </label>

              <label>
                Data
                <input
                  type="date"
                  min="0001-01-01"
                  max="9999-12-31"
                  required
                  disabled={busy}
                  value={editor.transactionDate}
                  onChange={(event) => setEditor({
                    ...editor, transactionDate: event.currentTarget.value,
                  })}
                />
              </label>

              <label>
                Descrição
                <input
                  type="text"
                  maxLength={255}
                  disabled={busy}
                  value={editor.description}
                  onChange={(event) => setEditor({ ...editor, description: event.currentTarget.value })}
                />
              </label>

              {actionError && <p className="notice notice-error" role="alert">{actionError}</p>}

              <div className="editor-actions">
                <button
                  className="button"
                  type="button"
                  disabled={busy}
                  onClick={() => {
                    setActionError('')
                    setEditor(null)
                  }}
                >
                  Cancelar
                </button>
                <button className="button button-primary" type="submit" disabled={busy}>
                  {busy ? 'Salvando…' : 'Salvar lançamento'}
                </button>
              </div>
            </form>
          </section>
        )}
      </main>
    </div>
  )
}