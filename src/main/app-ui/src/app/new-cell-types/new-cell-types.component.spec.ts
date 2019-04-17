import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { NewCellTypesComponent } from './new-cell-types.component';

describe('NewCellTypesComponent', () => {
  let component: NewCellTypesComponent;
  let fixture: ComponentFixture<NewCellTypesComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ NewCellTypesComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(NewCellTypesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
