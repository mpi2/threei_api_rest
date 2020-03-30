import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { HumanCellHeatmapComponent } from './human-cell-heatmap.component';

describe('HumanCellHeatmapComponent', () => {
  let component: HumanCellHeatmapComponent;
  let fixture: ComponentFixture<HumanCellHeatmapComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ HumanCellHeatmapComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(HumanCellHeatmapComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
