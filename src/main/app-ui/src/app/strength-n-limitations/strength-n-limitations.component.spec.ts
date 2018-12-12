import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { StrengthNLimitationsComponent } from './strength-n-limitations.component';

describe('StrengthNLimitationsComponent', () => {
  let component: StrengthNLimitationsComponent;
  let fixture: ComponentFixture<StrengthNLimitationsComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ StrengthNLimitationsComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(StrengthNLimitationsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
