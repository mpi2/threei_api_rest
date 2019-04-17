import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { AcknowledgeThreeiComponent } from './acknowledge-threei.component';

describe('AcknowledgeThreeiComponent', () => {
  let component: AcknowledgeThreeiComponent;
  let fixture: ComponentFixture<AcknowledgeThreeiComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ AcknowledgeThreeiComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(AcknowledgeThreeiComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
