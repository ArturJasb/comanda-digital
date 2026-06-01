import { Component, OnInit } from '@angular/core';
import { ChartConfiguration, Chart, registerables } from 'chart.js';
import { DashboardService } from '../../../core/services/dashboard.service';
import { DashboardResumo, TopPrato } from '../../../core/models/models';

Chart.register(...registerables);

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.scss'
})
export class DashboardComponent implements OnInit {
  resumo?: DashboardResumo;
  carregando = true;

  barData: ChartConfiguration<'bar'>['data'] = { labels: [], datasets: [] };
  barOptions: ChartConfiguration<'bar'>['options'] = {
    responsive: true, maintainAspectRatio: false,
    plugins: { legend: { display: false } },
    scales: { y: { beginAtZero: true, ticks: { precision: 0 } } }
  };

  lineData: ChartConfiguration<'line'>['data'] = { labels: [], datasets: [] };
  lineOptions: ChartConfiguration<'line'>['options'] = {
    responsive: true, maintainAspectRatio: false,
    plugins: { legend: { display: false } },
    scales: { y: { beginAtZero: true } }
  };

  constructor(private dashboardService: DashboardService) {}

  ngOnInit(): void {
    this.dashboardService.resumo().subscribe({
      next: r => {
        this.resumo = r;
        this.carregando = false;
        this.montarLinha(r);
      },
      error: () => { this.carregando = false; }
    });
    this.dashboardService.topPratos(30).subscribe(top => this.montarBarra(top));
  }

  private montarBarra(top: TopPrato[]): void {
    this.barData = {
      labels: top.map(t => t.nome),
      datasets: [{
        data: top.map(t => t.quantidade),
        label: 'Vendidos',
        backgroundColor: '#B11226',
        borderRadius: 6
      }]
    };
  }

  private montarLinha(r: DashboardResumo): void {
    this.lineData = {
      labels: r.vendasPorDia.map(v => this.formatDia(v.dia)),
      datasets: [{
        data: r.vendasPorDia.map(v => v.total),
        label: 'Faturamento',
        borderColor: '#0B8A66',
        backgroundColor: 'rgba(11,138,102,.15)',
        fill: true,
        tension: 0.35,
        pointBackgroundColor: '#0B8A66'
      }]
    };
  }

  private formatDia(dia: string): string {
    const d = new Date(dia + 'T00:00:00');
    return isNaN(d.getTime()) ? dia : d.toLocaleDateString('pt-BR', { day: '2-digit', month: '2-digit' });
  }
}
