import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { StatsBlocksComponent } from './stats-blocks.component';

describe('StatsBlocksComponent', () => {
  let component: StatsBlocksComponent;
  let fixture: ComponentFixture<StatsBlocksComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ StatsBlocksComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(StatsBlocksComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
