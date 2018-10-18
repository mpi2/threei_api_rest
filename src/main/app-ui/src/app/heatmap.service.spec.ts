import { TestBed, inject } from '@angular/core/testing';
import { Injectable } from '@angular/core';
import { HttpClientModule, HttpClient } from '@angular/common/http';
import { HeatmapService } from '../app/heatmap.service';
import { HttpErrorResponse, HttpResponse } from '@angular/common/http';
import { catchError, retry } from 'rxjs/operators';
import { Observable, Subject, ReplaySubject, from, of, range } from 'rxjs';
import { ProcedureFilter } from './procedure-heatmap/procedure-filter';
import { CellFilter } from './cell-heatmap/cell-filter';
import { environment } from '../environments/environment';

describe('HeatmapService', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClient
      ],
      providers: [HttpClient,HeatmapService]
    });
  });

  it('should be created', inject([HeatmapService], (service: HeatmapService) => {
    expect(service).toBeTruthy();
  }));
});
