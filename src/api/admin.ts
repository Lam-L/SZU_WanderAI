import axios from 'axios'

// Merchants
export const getMerchants = async () => {
  const response = await axios.get('/api/admin/merchants')
  return response.data
}

export const approveMerchant = async (merchantId: number) => {
  const response = await axios.post(`/api/admin/merchants/${merchantId}/approve`)
  return response.data
}

export const rejectMerchant = async (merchantId: number) => {
  const response = await axios.post(`/api/admin/merchants/${merchantId}/reject`)
  return response.data
}

export const blockMerchant = async (merchantId: number) => {
  const response = await axios.post(`/api/admin/merchants/${merchantId}/block`)
  return response.data
}

export const unblockMerchant = async (merchantId: number) => {
  const response = await axios.post(`/api/admin/merchants/${merchantId}/unblock`)
  return response.data
}

export const deleteMerchant = async (merchantId: number) => {
  await axios.delete(`/api/admin/merchants/${merchantId}`)
}

// Tour Packages
export const getPackages = async () => {
  const response = await axios.get('/api/admin/packages')
  return response.data
}

export const approvePackage = async (packageId: number) => {
  const response = await axios.post(`/api/admin/packages/${packageId}/approve`)
  return response.data
}

export const rejectPackage = async (packageId: number) => {
  const response = await axios.post(`/api/admin/packages/${packageId}/reject`)
  return response.data
}

export const deletePackage = async (packageId: number) => {
  await axios.delete(`/api/admin/packages/${packageId}`)
}

// Users
export const getUsers = async () => {
  const response = await axios.get('/api/admin/users')
  return response.data
}

export const blockUser = async (userId: number) => {
  const response = await axios.post(`/api/admin/users/${userId}/block`)
  return response.data
}

export const unblockUser = async (userId: number) => {
  const response = await axios.post(`/api/admin/users/${userId}/unblock`)
  return response.data
}

export const deleteUser = async (userId: number) => {
  await axios.delete(`/api/admin/users/${userId}`)
}

// Posts
export const getPosts = async () => {
  const response = await axios.get('/api/admin/posts')
  return response.data
}

export const hidePost = async (postId: number) => {
  const response = await axios.post(`/api/admin/posts/${postId}/hide`)
  return response.data
}

export const showPost = async (postId: number) => {
  const response = await axios.post(`/api/admin/posts/${postId}/show`)
  return response.data
}

export const deletePost = async (postId: number) => {
  await axios.delete(`/api/admin/posts/${postId}`)
}