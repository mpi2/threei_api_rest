import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { MapOfImmuneComponent } from './map-of-immune.component';

describe('MapOfImmuneComponent', () => {
  let component: MapOfImmuneComponent;
  let fixture: ComponentFixture<MapOfImmuneComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ MapOfImmuneComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(MapOfImmuneComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
