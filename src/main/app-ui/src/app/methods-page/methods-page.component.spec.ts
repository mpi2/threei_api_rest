import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { MethodsPageComponent } from './methods-page.component';

describe('MethodsPageComponent', () => {
  let component: MethodsPageComponent;
  let fixture: ComponentFixture<MethodsPageComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ MethodsPageComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(MethodsPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
