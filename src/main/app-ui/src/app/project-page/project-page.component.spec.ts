import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ConsortiumPageComponent } from './consortium-page.component';

describe('ConsortiumPageComponent', () => {
  let component: ConsortiumPageComponent;
  let fixture: ComponentFixture<ConsortiumPageComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ConsortiumPageComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ConsortiumPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
