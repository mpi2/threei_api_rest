import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { FlowFilesComponent } from './flow-files.component';

describe('FlowFilesComponent', () => {
  let component: FlowFilesComponent;
  let fixture: ComponentFixture<FlowFilesComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ FlowFilesComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(FlowFilesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
