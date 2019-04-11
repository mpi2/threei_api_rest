import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { WebstatusComponent } from './webstatus.component';

describe('WebstatusComponent', () => {
  let component: WebstatusComponent;
  let fixture: ComponentFixture<WebstatusComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ WebstatusComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(WebstatusComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
