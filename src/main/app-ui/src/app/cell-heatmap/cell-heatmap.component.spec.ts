import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { CellHeatmapComponent } from './cell-heatmap.component';

describe('CellHeatmapComponent', () => {
  let component: CellHeatmapComponent;
  let fixture: ComponentFixture<CellHeatmapComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ CellHeatmapComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(CellHeatmapComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
