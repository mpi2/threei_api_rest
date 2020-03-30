import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { HumanHomeComponent } from './human-home.component';

describe('HumanHomeComponent', () => {
  let component: HumanHomeComponent;
  let fixture: ComponentFixture<HumanHomeComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ HumanHomeComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(HumanHomeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
