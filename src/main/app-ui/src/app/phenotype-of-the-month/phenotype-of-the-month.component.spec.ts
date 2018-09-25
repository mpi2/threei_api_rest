import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { PhenotypeOfTheMonthComponent } from './phenotype-of-the-month.component';

describe('PhenotypeOfTheMonthComponent', () => {
  let component: PhenotypeOfTheMonthComponent;
  let fixture: ComponentFixture<PhenotypeOfTheMonthComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ PhenotypeOfTheMonthComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(PhenotypeOfTheMonthComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
