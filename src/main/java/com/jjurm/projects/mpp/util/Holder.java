package com.jjurm.projects.mpp.util;

public class Holder<T> {

  protected T value;

  public Holder(T value) {
    this.value = value;
  }

  public T get() {
    return value;
  }

  public void set(T value) {
    this.value = value;
  }

}
