import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ProcedureHeatmapComponent } from './procedure-heatmap.component';

describe('ProcedureHeatmapComponent', () => {
  let component: ProcedureHeatmapComponent;
  let fixture: ComponentFixture<ProcedureHeatmapComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ProcedureHeatmapComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ProcedureHeatmapComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
