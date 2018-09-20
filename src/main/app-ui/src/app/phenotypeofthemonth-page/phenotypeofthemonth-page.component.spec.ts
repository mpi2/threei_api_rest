import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { PhenotypeofthemonthPageComponent } from './phenotypeofthemonth-page.component';

describe('PhenotypeofthemonthPageComponent', () => {
  let component: PhenotypeofthemonthPageComponent;
  let fixture: ComponentFixture<PhenotypeofthemonthPageComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ PhenotypeofthemonthPageComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(PhenotypeofthemonthPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
